(ns cldwalker.logseq-clis.util.write
  "Utils for writing logseq markdown files")

(defn- str-node [node level {:keys [prefix] :as opts}]
  (apply str
         (concat (repeat level "\t")
                 [prefix (:name node) "\n"]
                 (mapv #(str-node % (inc level) opts) (:children node)))))

(defn build-tree
  "Builds a nested set of logseq blocks for one top-level block given
a data structure:
[{:name \"Thing\" :children [{:name \"Organization\" ...}] ...}]"
  ([tree] (build-tree tree {}))
  ([tree opts]
   (let [node-opts (merge opts {:prefix "- "})]
     (apply str (mapv #(str-node % 0 node-opts) tree)))))
