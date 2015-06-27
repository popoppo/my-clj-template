(defproject guestbook "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/clojurescript "0.0-3269"]
                 ;[org.clojure/clojurescript "0.0-2197"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [org.clojure/tools.reader "0.9.1"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.5.0"]
                 [enlive "1.1.5"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.65"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.3"]
                 [noir-exception "0.2.3"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [ragtime "0.3.8"]
;                 [yesql "0.5.0-rc1"]
                 [korma "0.4.0"]
                 [com.h2database/h2 "1.4.182"]
                 [reagent "0.5.0"]
                 [reagent-forms "0.5.0"]
                 [reagent-utils "0.1.4"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.3.11"]]

  :min-lein-version "2.0.0"
  :uberjar-name "guestbook.jar"
  :jvm-opts ["-server"]

  :main guestbook.core
;  :bootclasspath true

  :plugins [[lein-ancient "0.6.5"]
            [lein-cljsbuild "1.0.5"]
            ;[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]
            [lein-figwheel "0.3.3"]
            [lein-ring "0.9.1"]
            [lein-pprint "1.1.1"]
            [refactor-nrepl "1.0.5"]
            [cider/cider-nrepl "0.9.0-SNAPSHOT"]
            [ragtime/ragtime.lein "0.3.8"]]

;  :ring {
;         :init    guestbook.handler/init
;         :handler guestbook.handler/app
;         :destroy guestbook.handler/destroy
;         :uberwar-name "guestbook.war"}

  :ragtime
  {:migrations ragtime.sql.files/migrations
   :database "jdbc:h2:./resources/db/site.db"}


  :clean-targets ^{:protect false} ["resources/public/js" "target"]


  :cljsbuild
  {:builds [{:id "dev"
;             :source-paths ["src-cljs" "env/dev/cljs"]
             :source-paths ["src-cljs"]
             :figwheel {:websocket-host "" ; ~(System/getenv "WS_HOST");
                                        ;:on-jsload "...."
                        }
             :compiler {
                        ;:asset-path "js/out"
                        :externs ["react/externs/react.js"]
                        :optimizations :none
                        :output-to "resources/public/js/app.js"
                        :output-dir "resources/public/js/out"
                        :source-map true
                        :pretty-print true}}
            {:id "app"
             :source-paths ["src-cljs"]
             :compiler {:externs ["react/externs/react.js"]
                        :optimizations :none
                        :output-to "resources/public/js/app.js"
                        ;:output-dir "resources/public/js/out"
                        :pretty-print true}}]}

  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7888
   ;; :ring-handler guestbook.handler/app
   :css-dirs ["resources/public/css"]}

  :profiles
  {:uberjar {:omit-source true
             :hooks [leiningen.cljsbuild]
             :cljsbuild
             {:jar true
              :builds {:app {:source-paths ["env/prod/cljs"]
                             :compiler {:optimizations :advanced
                                        :pretty-print false}}}}
             :aot :all}

   ; Development
   :dev-env {} ;Put :dev-env to profiles.clj

   :dev-params
   {:dependencies [[org.clojure/tools.namespace "0.2.10"]
                   [org.clojure/tools.nrepl "0.2.10"]
                   [ring-mock "0.1.5"]
                   [ring/ring-devel "1.3.2"]
                   [pjstadig/humane-test-output "0.7.0"]]

    :source-paths ["env/dev/clj"]

    :repl-options {:init-ns user
                   ;:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                   :timeout 120000} ; default is 30000 (30 seconds)

    :injections [(require 'pjstadig.humane-test-output)
                 (pjstadig.humane-test-output/activate!)]}

   :dev [:dev-env :dev-params]
   }
  )
