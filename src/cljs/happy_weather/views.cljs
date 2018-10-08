(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [happy-weather.subs :as subs]))

(defn refresh-button [app-state]
  [:button {}
   "Refresh"])



(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [refresh-button]]))
