(ns guestbook.util.log
  (:use
   [clojure.string :only [join upper-case]] )
  (:require
   [taoensso.timbre :as timbre]))

(defn map->ltsv [map-arg & {:keys [infix] :or {infix ":"}}]
  (->> (map
        #(format "%s%s%s" (name %1) infix %2)
        (keys map-arg) (vals map-arg))
       (join "\t")))

(defn msg->ltsv [msg & [params]]
  (if params
    (str msg "\t" (map->ltsv params))
    msg))

(defmacro ltsv-log [level msg & [opt-map]]
  `(~(symbol (str "taoensso.timbre/" (name level) "f")) (msg->ltsv ~msg ~opt-map)))

(defmacro ll
  ([level msg & [opt-map]]
   `(ltsv-log ~level ~msg ~opt-map)))

(defn ltsv-fmt-output-fn
  [{:keys [level throwable message timestamp hostname ns]}
   ;; Any extra appender-specific opts:
   & [{:keys [nofonts?] :as appender-fmt-output-opts}]]
  ;; time:<time>\thost:<hostname>\tlevel:<LEVEL>\tns:<ns>\tmsg:<message>\tthrowable:<throwable>
  (join
   "\t"
   (remove #(= % "")
           [(str "timestamp:" timestamp)
            (str "hostname:" hostname)
            (str "LEVEL:" (-> level name upper-case))
            (str "ns:" ns)
            (if message (str "msg:" message) "")
            (or (timbre/stacktrace throwable "\n" (when nofonts? {})) "")])))

