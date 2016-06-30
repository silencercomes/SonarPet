#!/usr/bin/env python3
#
# Copyright (c) 2015-2016 Techcable (Nicholas Schlabach)
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE

from subprocess import run, CalledProcessError, PIPE
import os
import sys
from os import path
import argparse
import re as regex
import shutil
from tempfile import NamedTemporaryFile
from enum import Enum, unique

# Supported version enum
# We have to enumerate all supported NMS versions because the C preprocessor can't compare/handle strings
# However it _can_ handle integers, so all the versions are just marcos/definitions for integers
@unique
class SupportedVersions(Enum):
    v1_8_R3 = 0
    v1_9_R1 = 1
    v1_9_R2 = 2
    v1_10_R1 = 3

def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

parser = argparse.ArgumentParser(
    description="Generates version-specific NMS-dependent sources from a series of source files")
parser.add_argument('versions', help="The nms versions to generate sources for.", nargs='+')
parser.add_argument('--define', '-D', help="Defines an argument for the preprocessor/template processor.",
                    action="append", dest='definitions', default=[])
parser.add_argument('--templates', help="The directory to find the template files to generate sources from.",
                    default="templates", dest='template_dir')
parser.add_argument('--out-dir', help="What to output template files into ", default="$NMS_VERSION/src/main/java")

args = parser.parse_args()

if not path.exists(args.template_dir):
    eprint(args.template_dir, "doesn't exist")
    exit(1)
elif not path.isdir(args.template_dir):
    eprint(args.template_dir, "is not a directory")
    exit(1)

versions = list()

for version_name in args.versions:
    try:
        versions.append(SupportedVersions[version_name])
    except KeyError:
        eprint("Unknown version", version_name)
        exit(1)

definition_regex = regex.compile("(\w+)(?:=(\w+))")
variable_replacement_regex=regex.compile("\$(\w+)")
directive_regex=regex.compile("^\s*#.*")

if not shutil.which("cpp"):
    eprint("No c preprocessor found!")
    exit(1)

# Precompute the arguments to pass to the preprocessor for speed
cmd_args = [
    "cpp",
    "-P", # Don't generate weird linemarkers
    "-undef",  # Do not predefine any system-specific or GCC-specific macros.
    "-I", # Search for headers in the template dir
    args.template_dir
]

definitions = {"NMS_VERSION": None}

for definition in args.definitions:
    match = definition_regex.match(definition)
    if not match:
        eprint("Invalid definition", definition)
        exit(1)
    definition_name = match.group(1)
    definition_value = match.group(2) or True
    if definition_name in definitions:
        eprint("Can't redefine", definition_name,  "as", definition_value)
        exit(1)
    cmd_args.append('-D')
    cmd_args.append(definition)
    definitions[definition_name] = definition_value

for version in SupportedVersions:
    cmd_args.append("-D")
    cmd_args.append(version.name + "=" + str(version.value))

# Append a null as a placeholdersfor the nms version id
cmd_args.append("-D")
cmd_args.append(None)
cmd_args.append('-') # Read from stdin
cmd_args.append('-') # Write to stdout

def preprocess(input_file: str, output_file: str):
    try:
        input_lines = list()
        # We have to replace all occurrences of 'NMS_VERSION' outside of a preprocessor definition with the version manually
        # This is because NMS_VERSION is an integer because the preprocessor can't properly replace strings
        with open(input_file, 'rt') as file:
            for line in file:
                # Only treat NMS_VERSION as a string outside of a definition because the preprocessor can't handle string comparison
                if not directive_regex.match(line):
                    line = line.replace("NMS_VERSION", "NMS_VERSION_STRING")
                input_lines.append(line)
        output_str=run(cmd_args, check=True, universal_newlines=True, input='\n'.join(input_lines), stdout=PIPE).stdout
        # Defer replacement of 'NMS_VERSION_STRING' until now because otherwise the preprocessor will replace it with the magic id
        output_str = output_str.replace("NMS_VERSION_STRING", definitions["NMS_VERSION"])
        with open(output_file, 'wt+') as file:
            file.write(output_str)
    except CalledProcessError as e:
        eprint("Unable to preprocess file", input_file)
        exit(max(e.returncode, 0))


for version in versions:
    definitions["NMS_VERSION"] = version.name
    cmd_args[-3] = "NMS_VERSION=" + str(version.value) # Use the special/magic integer value because the preprocessor can't handle string comparison
    for dirpath, _, files in os.walk(args.template_dir):
        for file in files:
            file = path.join(dirpath, file)
            if file.endswith('.h'):
                print("Found header file", file)
                continue
            elif file.endswith('.template'):
                header_file = False
                print("Processing template file", file, "for version", version.name)
                output_file = path.join(args.out_dir, path.relpath(file, start=args.template_dir)[:-len('.template')])
                output_file = variable_replacement_regex.sub(lambda var_match: definitions[var_match.group(1)], output_file)
                os.makedirs(path.dirname(output_file), exist_ok=True)
                preprocess(file, output_file)
            else:
                eprint("Unable to process file", file, "because it isn't a template file!")
                eprint("Please prefix it with '.template' to use it as a template")
                exit(1)
