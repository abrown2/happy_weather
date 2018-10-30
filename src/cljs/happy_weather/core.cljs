(ns happy-weather.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [happy-weather.events :as events]
   [happy-weather.views :as views]
   [happy-weather.config :as config]))



(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app"))
  (views/clockon))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
