(ns guestbook.handler.app
  (:require
   [compojure.core :refer [routes]]
   [environ.core :refer [env]]
   [guestbook.routes.home :refer [base-routes home-routes]]
   [guestbook.util.session :as session]
   [prone.middleware :refer [wrap-exceptions]]
   [ring.util.response :refer [redirect]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
   [ring.middleware.session.memory :refer [memory-store]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [selmer.middleware :refer [wrap-error-page]]
   [taoensso.timbre :as timbre]))


(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (timbre/error t)
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body "<body>
                  <h1>Something very bad has happened!</h1>
                  <p>We've dispatched a team of highly trained gnomes to take care of the problem.</p>
                </body>"}))))


(defn development-middleware [handler]
  (if (env :dev)
    (-> handler
        wrap-error-page
        wrap-exceptions)
    handler))


(defn production-middleware [handler]
  (-> handler

      (wrap-restful-format :formats [:json-kw :edn :transit-json :transit-msgpack])
      (wrap-idle-session-timeout
        {:timeout (* 60 30)
         :timeout-response (redirect "/")})
      (wrap-defaults
        (assoc-in site-defaults [:session :store] (memory-store session/mem)))
      wrap-internal-error))


(def app
  (-> (routes
       home-routes
       base-routes)
      development-middleware
      production-middleware))
