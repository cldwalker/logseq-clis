(ns cldwalker.logseq-clis.cli.logseq-class-hierarchy
  (:require [babashka.cli :as cli]
            [logseq.db.rules :as rules]
            [datascript.core :as d]
            [cldwalker.logseq-clis.util.write :as write-util]
            [cldwalker.logseq-clis.util :as util]
            ["fs" :as fs]))

(defn- ->ancestors [m elem]
  (loop [cur elem res []] (if-let [new-cur (m cur)] (recur new-cur (conj res new-cur)) res)))

(defn- ->tree-children [m e name-transform]
  (mapv #(let [ch (->tree-children m % name-transform)]
           (cond-> {:name (name-transform %)}
             (seq ch)
             (assoc :children (sort-by :name ch))))
        (m e)))

(defn- parents-map->nodes
  ([parents_] (parents-map->nodes parents_ identity))
  ([parents_ name-transform]
   (let [roots (set (keep last (map (partial ->ancestors parents_) (keys parents_))))
         children-map (into {} (map (fn [[k v]]
                                      [k (mapv first v)]) (group-by val parents_)))
         nodes (mapv
                (fn [root]
                  {:name (name-transform root)
                   :children (sort-by :name (->tree-children children-map root name-transform))})
                roots)]
     nodes)))

(defn- print-class-tree [result]
  (let [ents (map #(-> (:block/properties %)
                       (assoc :name (:block/original-name %))
                       (dissoc :type))
                  result)
        alias-map (into {} (keep #(when (seq (:alias %))
                                    [(first (:alias %)) (:name %)])
                                 ents))
        parents-map (into {}
                          (map #(vector (:name %)
                                        (let [parent (first (:parent %))]
                                          (alias-map parent parent)))
                               ents))
        result-tree (parents-map->nodes parents-map #(str "[[" % "]]"))]
    (print (write-util/build-tree result-tree))))

(def spec
  "CLI options"
  {:cache-directory {:alias :c}
   :directory {:alias :d
               :desc "Graph directory"
               :default "."}
   :help {:alias :h
          :desc "Print help"}})

(defn- write-class-file
  [file {:keys [directory cache-directory]}]
  (let [db (util/get-db directory cache-directory)
        results (map first
                     (d/q '[:find (pull ?b [* {:block/alias [*]}])
                            :in $ %
                            :where
                            (page-property ?b :type "Class")]
                          db
                          (vals rules/query-dsl-rules)))
        body (with-out-str (print-class-tree results))]
    (println "Writing" (count results) "classes to" file)
    (fs/writeFileSync file body)))

(defn -main [& args]
  (let [{:keys [help] :as options} (cli/parse-opts args {:spec spec})
        _ (when (or help (zero? (count args)))
            (println (str "Usage: logseq-class-hierarchy FILE [OPTIONS]\nOptions:\n"
                          (cli/format-opts {:spec spec})))
            (js/process.exit 1))]
    (write-class-file (first args) options)))

#js {:main -main}
