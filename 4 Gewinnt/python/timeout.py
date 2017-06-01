
import signal
import time
from functools import wraps
import sys

class TimeoutException(Exception):
    pass


def timethis(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        def timeout_handler(signum, frame):
            raise TimeoutException('Valid Time Expired')

        signal.signal(signal.SIGALRM, timeout_handler)
        timeout_time = kwargs.pop("timeout", 0)
        signal.alarm(timeout_time)
        start = time.time()
        result = func(*args, **kwargs)
        elapsed = round(time.time()-start, 3)
        return result
    return wrapper

def timelimit(limit, func, args = [], kwargs = {}):
    if sys.platform.startswith('linux'):
        kwargs['timeout'] = limit
        return timethis(func)(*args, **kwargs)
    else:
        return func(*args, **kwargs)
