(ns guestbook.core
  (:require [cronj.core :as cronj]
            [environ.core :refer [env]]
            [guestbook.db.core :as db]
            [guestbook.handler.app :refer [app]]
            [guestbook.util.session :as session]
            [guestbook.util.log :refer [ltsv-fmt-output-fn]]
            [ring.adapter.jetty :refer [run-jetty]]
            [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            )
  (:gen-class))


(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/set-config!
    [:fmt-output-fn]
    ltsv-fmt-output-fn)

  (timbre/set-config!
    [:appenders :rotor]
    {:min-level             :debug
     :enabled?              true
     :async?                false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn                    rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "guestbook.log" :max-size (* 512 1024) :backlog 10})

  (when (env :dev)
    (parser/cache-off!))

  ;; db connection via korma
  (db/connect-db)

  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "guestbook started successfully"
               (when (env :dev)
                 "with dev profile")))


(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "guestbook is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))


(defn get-port []
  (or (env :server-port) 3000))


(defn -main [args]
  (let [port (get-port)]
    ;; TODO logging params
;    (init)
    ;; TODO Where should destory be?
    (run-jetty app {:port port :join? false})))
