(ns guestbook.routes.home
  (:use
   korma.core)
  (:require
   [clojure.java.io :as io]
   [compojure.core :refer [defroutes GET]]
   [compojure.route :as route]
   [guestbook.handler.layout :as layout]))


; Base
(defroutes base-routes
           (route/resources "/")
           (route/not-found "Not Found"))

; Home
(defn top-page []
  (reduce str (layout/top-page)))

(defn top-page-with-param []
  (reduce str (layout/top-page-with-param layout/sample-top)))

(defn top-page-with-param2 []
  (defentity users) ; users table
  (let [rec (select users)
        param {:author (:first_name (first rec))
               :title "Title"
               :body (:email (first rec))}]
    (reduce str (layout/top-page-with-param param))))

(defroutes home-routes
  (GET "/" [] (top-page))
  (GET "/foo" [] (top-page-with-param))
  (GET "/bar" [] (top-page-with-param2)))
