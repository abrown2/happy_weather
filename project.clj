(defproject happy-weather "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [cljsjs/react "16.5.2-0"]
                 [cljsjs/react-dom "16.5.2-0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljsjs/spin "2.3.2-0"]
                 [lein-cljsbuild "1.1.7"]
                 [cljsjs/react-pose "1.6.4-0"]]



  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]

    :plugins      [[lein-figwheel "0.5.16"]]}
   :prod {}}


  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "happy_weather.core/mount-root"}
     :compiler     {:main                 happy_weather.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}


    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            happy_weather.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
