import asyncio
import sys
import getopt
import json
import uuid

from yapapi.log import enable_default_logger

import utils
from core import TranscodingParameters, GolemParameters, TranscodingData, TranscodingFinishedEvent
from task import SubtaskGenerator
from engine import TranscodingEngine


class TranscodingProcessor:
    """Based on parameters creates subtasks and passes them to engine for execution
    
    ...

    Attributes
    ----------
    subtask_generator : SubtaskGenerator
        creates transcoding subtasks
    engine : TranscodingEngine
        link to the Golem platform
    """

    def __init__(self, subtask_generator: SubtaskGenerator, engine: TranscodingEngine):
        self.subtask_generator = subtask_generator
        self.engine = engine

    @staticmethod
    async def instance(golem_parameters: GolemParameters):
        """Creates processor with default settings"""
        return TranscodingProcessor(SubtaskGenerator(), await TranscodingEngine.instance(golem_parameters))

    async def transcode(self, parameters: TranscodingParameters):
        """Generates transcoded videos using Golem
        
        Parameters
        ----------
        parameters : TranscodingParameters
            Input file details
        """
        tasks: [TranscodingData] = self.subtask_generator.create_tasks(parameters)
        await self.engine.execute(tasks)
        event = TranscodingFinishedEvent(str(uuid.uuid4()), parameters.transcoding_id)
        print(json.dumps(event.__dict__))


def print_help():
    print('requestor.py -i <filename> -t <transcoding ID>')


def parse_params(argv) -> TranscodingParameters:
    filepath: str = ''
    transcoding_id: str = ''
    try:
        opts, args = getopt.getopt(argv, "hi:t:l:")
    except getopt.GetoptError:
        print_help()
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print_help()
            sys.exit()
        elif opt == '-i':
            filepath = arg
        elif opt == '-t':
            transcoding_id = arg

    return TranscodingParameters(transcoding_id, filepath)


async def main(argv):
    """Parsing arguments and executing the logic"""
    params: TranscodingParameters = parse_params(argv)
    enable_default_logger()
    # prepare default values for golem parameters
    subnet_tag = 'devnet-alpha.2'
    max_workers = 10
    budget = 10.0
    golem_details: GolemParameters = GolemParameters(subnet_tag, max_workers, budget)
    processor: TranscodingProcessor = await TranscodingProcessor.instance(golem_details)
    await processor.transcode(params)


def asyncio_loop_setup(coroutine):
    """Setting up the event loop for any coroutine"""
    # This is only required when running on Windows with Python prior to 3.8:
    utils.windows_event_loop_fix()

    loop = asyncio.get_event_loop()
    task = loop.create_task(coroutine)

    try:
        loop.run_until_complete(task)
    except (Exception, KeyboardInterrupt) as e:
        print(e)
        task.cancel()
        loop.run_until_complete(task)


# script entrypoint
if __name__ == "__main__":
    coroutine = main(sys.argv[1:])
    asyncio_loop_setup(coroutine)

