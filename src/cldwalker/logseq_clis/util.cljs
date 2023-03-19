(ns cldwalker.logseq-clis.util
  (:require [clojure.string :as string]
            [logseq.graph-parser.mldoc :as gp-mldoc]
            [datascript.transit :as dt]
            [logseq.graph-parser.cli :as gp-cli]
            ["path" :as path]
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

(defn get-db
  "If cached db exists get it, otherwise parse for a fresh db"
  [graph-dir cache-dir]
  ;; cache-db from https://github.com/logseq/bb-tasks
  (let [cache-file (path/join (or cache-dir ".") ".cached-db-transit.json")]
    (if (fs/existsSync cache-file)
      (do
        (println "Reading from cached db")
        (dt/read-transit-str (fs/readFileSync cache-file)))
      (let [{:keys [conn]} (gp-cli/parse-graph graph-dir {:verbose false})] @conn))))
