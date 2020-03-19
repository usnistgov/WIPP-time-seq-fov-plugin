#!/bin/bash
version=$(<VERSION)
docker build . -t wipp/wipp-time-seq-fov-plugin:${version}
