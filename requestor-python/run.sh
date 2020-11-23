#!/bin/bash
source ./venv/bin/activate
python -u ./requestor.py "$@" | tee -a ./requestor.log