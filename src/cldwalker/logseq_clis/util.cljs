(ns cldwalker.logseq-clis.util
  (:require [clojure.string :as string]
            [logseq.graph-parser.mldoc :as gp-mldoc]
            ["fs" :as fs]))

(defn strip-trailing-whitespace
  "Needed b/c export has trailing whitespace bug which started somewhere b/n
  mldoc 1.3.3 and 1.5.1"
  [s]
  (string/replace s #"\s+(\n|$)" "$1"))

(defn file-ast
  "Returns file ast for given file and mldoc config"
  [input-file config]
  (let [body (fs/readFileSync input-file)]
    (gp-mldoc/->edn (str body) config)))
