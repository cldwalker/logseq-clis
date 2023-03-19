## Description

An assortment of handy [logseq](https://github.com/logseq/logseq) CLIs using
[nbb-logseq](https://github.com/logseq/nbb-logseq).

## Prerequisites

* nodejs >= 16.3.1 and yarn or npm

## Setup

To use these clis/commands/executables on `$PATH`:

```sh
$ git clone https://github.com/cldwalker/logseq-clis
# Use yarn (what I use)
$ yarn install
$ yarn global add $PWD

# OR use npm
$ npm i
$ npm i -g
```

## Usage

The following CLIs are now installed.

### logseq-file-ast

Prints the logseq ast of a given markdown file:

```sh
$ logseq-file-ast example.md
[[["Heading"
   {:anchor "apple",
    :level 1,
    :meta {:properties [], :timestamps []},
    :size nil,
    :tags [],
    :title [["Plain" "apple "] ["Tag" [["Plain" "some-tag"]]]],
    :unordered true}]
  {:end_pos 18, :start_pos 0}]
 [["Heading"
   {:anchor "banana",
    :level 2,
    :meta {:properties [], :timestamps []},
    :size nil,
    :tags [],
    :title [["Plain" "banana "]
            ["Tag" [["Plain" "work"]]]
            ["Plain" " "]
            ["Tag" [["Plain" "blarg"]]]],
    :unordered true}]
  {:end_pos 41, :start_pos 18}]
...
```

### logseq-block-move

Moves blocks with specified tag to another file or directory. Useful for finely
splitting a logseq graph directory.

```sh
$ logseq-block-move pages new-pages unused-tag
pages/example.md -> new-pages/example.md - 6 of 8 nodes moved
```

**Note**: There are minor whitespace changes and [this bug](https://github.com/logseq/mldoc/issues/116) that occur during the move. It's recommended to use `logseq-roundtrip` on the intended move target beforehand to see what the mldoc exporter will do.

### logseq-roundtrip

Parses and then exports a markdown file. Useful for testing mldoc's export.

### logseq-class-hierarchy

Prints a graph's class hierarchy to a logseq-compatible markdown file. Useful
for generating a navigable tree for a graph's ontology a.k.a. "table of
contents".

## Development

Code is organized under the following directories:
* `bin/` -  Node scripts to install on `$PATH`
* `src/` - All cljs code used across scripts. Can be used as a gitlib.
  * `src/cldwalker/logseq_clis/cli` - Main namespaces for clis

## License
See LICENSE.md
