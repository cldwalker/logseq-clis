(ns cldwalker.logseq-clis.cli.logseq-roundtrip
  "For given file or directory, roundtrips mldoc library by parsing and then exporting it"
  (:require ["fs" :as fs]
            ["path" :as path]
            [cldwalker.logseq-clis.util :as util]
            [logseq.graph-parser.mldoc :as gp-mldoc]))

(defn- roundtrip-file [input-file output-file config]
  (let [md-ast (util/file-ast input-file config)]
    (fs/writeFileSync output-file
                      (util/strip-trailing-whitespace (gp-mldoc/ast-export-markdown (-> md-ast clj->js js/JSON.stringify) config gp-mldoc/default-references)))))

(defn -main*
  [args]
  (let [[input output] args
        config (gp-mldoc/default-config :markdown {:export-keep-properties? true})]
    (if (.isDirectory (fs/lstatSync input))
      (do
        (when-not (fs/existsSync output) (fs/mkdirSync output))
        (doseq [input-file (js->clj (fs/readdirSync input))]
          (roundtrip-file (path/join input input-file) (path/join output input-file) config)))
      (roundtrip-file input output config))))

(defn -main
  [& args]
  (if (not= 2 (count args))
    (println "Usage: logseq-roundtrip IN OUT\n\nGiven a markdown"
             "IN file, parses and exports it with mldoc and writes to OUT file.\nIf"
             "IN is a directory, all files in directory are written to OUT directory.")
    (-main* args)))

#js {:main -main}
