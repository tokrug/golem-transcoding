#!/bin/bash
source ./venv/bin/activate
python ./requestor.py "$@" | tee -a ./requestor.log