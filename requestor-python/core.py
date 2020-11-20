from dataclasses import dataclass
from yapapi.runner import Task


@dataclass
class TranscodingParameters:
    """Parameters received from the server"""
    transcoding_id: str
    filepath: str


@dataclass
class TranscodingData:
    """Data to be passed to a remote node"""
    transcoding_id: str
    input: str
    output: str
    extension: str


@dataclass
class GolemParameters:
    """Parameters for golem internals"""
    subnet_tag: str
    max_workers: int
    budget: float


@dataclass
class SubtaskFinishedEvent:
    id: str
    transcoding_id: str
    extension: str
    type: str = "subtask-finished"


@dataclass
class TranscodingFinishedEvent:
    id: str
    transcoding_id: str
    type: str = "finished"


# Alias for Yagna tasks with GeomandelData input and file path result
TranscodingTask = Task[TranscodingData, str]
