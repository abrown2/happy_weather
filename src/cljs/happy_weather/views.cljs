(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [happy-weather.subs :as subs]))

(defn refresh-button [app-state]
  [:button {}
    "Refresh"])

(defn wind-direction [app-state]
  (let [wind-direction (re-frame/subscribe [::subs/wind-direction])]
  ;;(let [wind-direction 123]
    [:div
      [:p "wind-direction=" @wind-direction]]))

(defn wind-direction-input
  []
  [:div.wind-direction-input
   "Enter new wind direction: "
   [:input {:type "text"
            :value @(re-frame/subscribe [::subs/wind-direction])
            :on-change #(re-frame/dispatch [:wind-direction-change (-> % .-target .-value)])}]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [wind-direction]
     [wind-direction-input]
     [refresh-button]]))
