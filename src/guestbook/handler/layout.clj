(ns guestbook.handler.layout
  (:require [net.cgrand.enlive-html :as html]
            [ring.util.response :refer [content-type response]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            ))

;; Define the templates
(html/deftemplate top-page "public/templates/top.html" [])

(html/deftemplate top-page-with-param "public/templates/top.html"
  [top]
  [:title] (html/content (:title top))
  [:h1] (html/content (:title top))
  [:span.author] (html/content (:author top))
  [:div.post-body] (html/content (:body top)))

;; Some sample data
(def sample-top {:author "Luke VanderHart"
                 :title "Why Clojure Rocks"
                 :body "Functional programming!"})
