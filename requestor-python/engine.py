from yapapi.runner import Engine, Task, vm
from yapapi.runner.ctx import WorkContext
from yapapi.log import log_summary, log_event_repr
from datetime import timedelta
import json
import uuid

from core import TranscodingData, TranscodingTask, GolemParameters, SubtaskFinishedEvent

# image hash of the geomandel docker image uploaded to golem
# _IMAGE_LINK = "896909125d8dc19918dc73fe7540ca45cfe87f434ed37f51edb20a4e"
# geomandel image
# _IMAGE_LINK = "47cd0f045333d837304d61f74266a1bcd49ad3cb0690a10f08d37bf4"
# ubuntu ffmpeg
_IMAGE_LINK = "febcd478b3e00b3d40a6d2a69a4932eedcc4440a1fe7658fbb626264"


class ImageRepository:
    async def get_image(self, minimal_memory: float, minimal_storage: float):
        """Retrieve a link to the ffmpeg docker image together with constraints"""
        return await vm.repo(
            image_hash=_IMAGE_LINK,
            min_mem_gib=minimal_memory,
            min_storage_gib=minimal_storage,
        )


class YagnaContext:
    """Holds information about the docker image and constraints for all the tasks to be executed in this context."""
    def __init__(self, package, max_workers: int, budget: float, subnet_tag: str):
        self.package = package
        self.max_workers = max_workers
        self.budget = budget
        self.subnet_tag = subnet_tag

    def __create_engine(self):
        """Creates yagna engine"""
        return Engine(
            package=self.package,
            max_workers=self.max_workers,
            budget=self.budget,
            timeout=timedelta(minutes=25),
            subnet_tag=self.subnet_tag,
            # By passing `event_emitter=log_summary()` we enable summary logging.
            # See the documentation of the `yapapi.log` module on how to set
            # the level of detail and format of the logged information.
            event_emitter=log_summary(log_event_repr),
        )   

    async def execute(self, tasks: [Task], worker_function, on_task_complete):
        """Executes a set of tasks on a preconfigured docker image.
        
        Parameters
        ----------
        tasks : [Task]
            Yagna tasks
        worker_function : (ctx: WorkContext, tasks) -> [Work]
            Function returning a sequence of instructions for each of the provided tasks.
        on_task_complete : (task: Task) -> None
            Callback executed when a task has been processed.
        """
        async with self.__create_engine() as engine:
            async for task in engine.map(worker_function, tasks):
                on_task_complete(task)


# docker image path to JSON file with task parameters
_TASK_INPUT_REMOTE_PATH = "/golem/work/input"

# minimal provider node memory constraint, not configurable
_MINIMAL_MEMORY = 0.5
# minimal provider node storage constraint, not configurable
_MINIMAL_STORAGE = 2.0


class TranscodingEngine:
    """Converts geomandel subtasks to yagna subtasks and sends them to Yagna Engine"""
    def __init__(self, yagna):
        self.yagna = yagna

    @staticmethod
    async def instance(golem_parameters: GolemParameters):
        """Creates an instance of TranscodingEngine. Static factory."""
        repository = ImageRepository()
        # retrieve the image link to ffmpeg docker image together with constraints
        package = await repository.get_image(_MINIMAL_MEMORY, _MINIMAL_STORAGE)
        # prepares the yagna engine
        yagna = YagnaContext(package, golem_parameters.max_workers, golem_parameters.budget, golem_parameters.subnet_tag)
        # wraps it in transcoding layer
        return TranscodingEngine(yagna)

    async def execute(self, tasks: [TranscodingData]):
        """Translates subtasks into Yagna format and executes them."""
        wrapped_tasks = self.__wrap_in_yagna_task(tasks)
        await self.yagna.execute(wrapped_tasks, self.__transcode_remote, self.__log_completion)

    async def __transcode_remote(self, ctx: WorkContext, tasks: [TranscodingTask]):
        """Creates a set of instructions for each subtask"""
        async for task in tasks:
            remote_output_path: str = f"/golem/work/output.{task.data.extension}"
            # Send input video to remote node
            ctx.send_file(task.data.input, _TASK_INPUT_REMOTE_PATH)
            # Execute ffmpeg command.
            ctx.run("/usr/bin/ffmpeg", "-i", _TASK_INPUT_REMOTE_PATH, remote_output_path)
            # Download the output file.
            ctx.download_file(remote_output_path, task.data.output)
            # Return a sequence of commands to be executed when remote node agrees to process a task.
            yield ctx.commit()
            task.accept_task()

    def __log_completion(self, task: TranscodingTask):
        event = SubtaskFinishedEvent(str(uuid.uuid4()), task.data.transcoding_id, task.data.extension)
        print(json.dumps(event.__dict__))

    def __wrap_in_yagna_task(self, data: []):
        """Converts any task data sequence to Yagna wrapper"""
        for item in data:
            yield Task(data=item)
