"""Utilities copied from yapapi examples."""
import asyncio
import sys
from pathlib import Path

TEXT_COLOR_RED = "\033[31;1m"
TEXT_COLOR_GREEN = "\033[32;1m"
TEXT_COLOR_YELLOW = "\033[33;1m"
TEXT_COLOR_BLUE = "\033[34;1m"
TEXT_COLOR_MAGENTA = "\033[35;1m"
TEXT_COLOR_CYAN = "\033[36;1m"
TEXT_COLOR_WHITE = "\033[37;1m"
TEXT_COLOR_DEFAULT = "\033[0m"

PROJECT_ROOT = Path(__file__).parent


def windows_event_loop_fix():
    """Set up asyncio to use ProactorEventLoop implementation for new event loops on Windows."""

    # For Python 3.8 ProactorEventLoop is already the default on Windows
    if sys.platform == "win32" and sys.version_info < (3, 8):

        class _WindowsEventPolicy(asyncio.events.BaseDefaultEventLoopPolicy):
            _loop_factory = asyncio.windows_events.ProactorEventLoop

        asyncio.set_event_loop_policy(_WindowsEventPolicy())
