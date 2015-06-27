(ns user
  (:use
   [ring.server.standalone :only (serve)]
   [ring.middleware file-info file])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [environ.core :refer (env)]
            [figwheel-sidecar.repl-api :refer (cljs-repl)]
            [guestbook.core :refer (init destroy)]
            [guestbook.handler.app :refer (app)]
            ))


;; Funcs for managing up/down server proc
(defonce server (atom nil))


(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body
      (wrap-file-info)))


(defn start-server
  "For starting a server in development mode from REPL"
  []
  (let [opts {:port (or (env :server-port) 3000)
              :init init
              :destroy destroy
              :open-browser false
              ;:browser-uri "templates"
              :join? false
              :stacktraces? true
              :auto-reload? true
              ;:reload-paths ["src"]
              ;:auto-refresh? true
              }]
    (reset! server (serve (get-handler) opts))))


(defn stop-server []
  (when @server
    (.stop @server))
  (reset! server nil))


;; Utils for dev
(def system nil)


(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system
                  (fn [s]
                    (start-server))))


(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s]
      (stop-server)
      (when s
        (print s)))))


(defn start-cljs-repl
  []
  (cljs-repl))


(defn reset []
  (stop)
  (refresh :after 'user/start))
