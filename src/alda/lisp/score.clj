(ns alda.lisp.score
  (:require [alda.lisp.score.part]))
(in-ns 'alda.lisp)

(log/debug "Loading alda.lisp.score...")

(defn score*
  []
  (letfn [(init [var val] (alter-var-root var (constantly val)))]
    (init #'*events* {:start {:offset (AbsoluteOffset. 0), :events []}})
    (init #'*global-attributes* {})
    (init #'*instruments* {})
    (init #'*current-instruments* #{})
    (init #'*nicknames* {})))

(defn event-set
  "Takes *events* in its typical form (organized by markers with relative
   offsets) and transforms it into a single set of events with absolute
   offsets."
  [events-map]
  (into #{}
    (mapcat (fn [[_ {:keys [offset events]}]]
              (for [event events]
                (update-in event [:offset] absolute-offset)))
            events-map)))

(defn markers [events-map]
  (into {}
    (map (fn [[marker-name {marker-offset :offset}]]
           [marker-name (absolute-offset marker-offset)])
         events-map)))

(defn score-map
  []
  {:events (event-set *events*)
   :markers (markers *events*)
   :instruments *instruments*})

(defmacro score
  "Initializes a new score, evaluates body, and returns the map containing the
   set of events resulting from evaluating the score, and information about the
   instrument instances, including their states at the end of the score."
  [& body]
  `(do
     (score*)
     ~@body
     (score-map)))
