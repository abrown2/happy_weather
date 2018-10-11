(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [happy-weather.subs :as subs]))

(defn get-location-button2
  []
  [:div {:class "button-class"
         :on-click  #(re-frame/dispatch [:location-retrieve])}  ;; get data from the server !!
        "Get Locations"])

(defn get-location-button
  []
  [:div
   [:button {:on-click  #(re-frame/dispatch [:location-retrieve])}  ;; get data from the server !!
           "Get Locations"]])


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

(defn locations[app-state]
  (let [locations (re-frame/subscribe [::subs/locations])]
  ;;(let [wind-direction 123]
    [:div
      [:p "locations=" @locations]]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [wind-direction]
     [wind-direction-input]
     [get-location-button]
     [locations]]))
