import utils
from core import TranscodingData, TranscodingParameters

# FORMATS = ['mp4']
FORMATS = ['mp4', 'mkv', 'avi', 'mov']


def _local_output_path(trans_id: str, extension: str):
    """Calculates absolute path to output file for a particular output video"""
    return f'{utils.PROJECT_ROOT}/output/{trans_id}/output.{extension}'


class SubtaskGenerator:
    """Generates subtask data"""
    def create_tasks(self, parameters: TranscodingParameters) -> [TranscodingData]:
        """Generates subtask data for each target video format
        
        Parameters
        ----------
        parameters : TranscodingParameters
            Input file details
        """

        return [self._create_task(parameters, extension) for extension in FORMATS]

    def _create_task(self, parameters: TranscodingParameters, extension: str):
        return TranscodingData(parameters.transcoding_id, parameters.filepath, _local_output_path(parameters.transcoding_id, extension), extension)
