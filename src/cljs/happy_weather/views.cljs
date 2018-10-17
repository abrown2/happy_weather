(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [happy-weather.subs :as subs]
   [cljsjs.spin :as spin]))

(defn get-location-button
  []
  [:div
   [:button {:on-click  #(re-frame/dispatch [:location-retrieve])}  ;; get data from the server !!
           "Get Locations"]])


(defn wind-direction [app-state]
  (let [wind-direction (re-frame/subscribe [::subs/wind-direction])]
    [:div
      [:p "wind-direction=" @wind-direction]]))

(defn wind-direction-input
  []
  [:div.wind-direction-input
   "Enter new wind direction: "
   [:input {:type "text"
            :value @(re-frame/subscribe [::subs/wind-direction])
            :on-change #(re-frame/dispatch [:wind-direction-change (-> % .-target .-value)])}]])

(defn location-list []
  (let [locations  @(re-frame/subscribe [::subs/locations])]
    [:ul#loc-list
     (for [location  (:Location locations)]
      [:li#loc-list "id: " (:id location) "; name:" (:name location)])]))

;;(defn spinner []
;;  (let [spinner (aget js/window "Spinner")]
;;   [(reagent/adapt-react-class spinner)
;;    (js-obj lines: 99)))    ;; need a js object as spinner mutates it



(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [wind-direction]
     [wind-direction-input]
     [:p]
     [get-location-button]
     [location-list]]))
