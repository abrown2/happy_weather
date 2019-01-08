(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [happy-weather.subs :as subs]
   [happy-weather.orbit :as orbit]
   [happy-weather.date-utils :as date-utils]
   [happy-weather.forecast :as forecast]
   [cljsjs.spin :as spin]
   [cljsjs.react :as react]
   [cljsjs.react.dom :as r-dom]
   [cljsjs.react-pose :as pose]
   [reagent.ratom :as ratom]
   [reanimated.core :as anim]))


(defn get-location-button
  []
  [:div
   [:button {:on-click  #(re-frame/dispatch [:location-retrieve])}  ;; get data from the server !!
           "Get Locations"]])


(defn location-list []
  (let [locations  @(re-frame/subscribe [::subs/locations])]
    [:ul#loc-list
     (for [location  (:Location locations)]
      [:li#loc-list "id: " (:id location) "; name:" (:name location)])]))


(defn get-forecast-button
  []
  [:div
   [:button {:on-click  #(re-frame/dispatch [:forecast-retrieve])}  ;; get data from the server !!
           "Get Ricky Forecast"]])



(defn forecast-raw []
  (let [target-slice  @(re-frame/subscribe [::subs/target-slice])]
    [:div "Forecast: "  target-slice]))


(defn cloud-cartoon []
  (let [tilt (reagent/atom 0)
        rotation (anim/spring tilt)
        flip (reagent/atom 90)
        scale (anim/spring flip)
        size (reagent/atom 0)
        width (anim/spring size)]
    (fn a-logo-component []
      [:div
       {:style {:position "absolute" :top "180px" :right "500px"}}
       [anim/interval #(reset! size 100) 300]
       [:img
        {:src "img/cloud-cartoon-med.png"
         :width (str @width "px")
         :style (zipmap [:-ms-transform
                         :-moz-transform
                         :-webkit-transform
                         :transform]
                        (repeat (str "rotate(" @rotation "deg) rotateY(" (+ 90 @scale) "deg)")))
         :on-mouse-over (fn logo-mouseover [e]
                          (reset! tilt 15))
         :on-mouse-out (fn logo-mouseout [e]
                         (reset! tilt 0))}]])))


(defn sun-cmpt[]

  (let [sun-pos (reagent/atom {:sun-pos (orbit/orbit {:deg 130 :r 210} 30)})
        x-pos (ratom/reaction (:x (:sun-pos @sun-pos)))
        x (anim/interpolate-to x-pos {:duration 500})
        y-pos (ratom/reaction (:y (:sun-pos @sun-pos)))
        y (anim/interpolate-to y-pos {:duration 500})]

   (fn create-sun []
        [:svg
         {:width 700
          :height 340}
         [anim/interval #(swap! sun-pos update :sun-pos orbit/update-orbit) 500]
         [:g
          {:transform (str "translate(" @x " " @y ")")}
          [:circle
           {:r 20
            :cx 0 :cy 0
            :fill "gold"}]]])))

(defn horizon[]
  [:svg
   {:width 800
    :height 2}
   [:g
    [:line
      {:x1 0 :y1 0 :x2 700 :y2 0 :style {:stroke "rgb(0,0,0)" :stroke-width "4"}}]]])

(defn time-status [app-state]
  (let [timer (re-frame/subscribe [::subs/timer])
        current-time (:current-time @timer)
        offset-hours (:offset-hours @timer)
        range-hours (:range-hours @timer)]
    [:div
      [:p "current-time=" (date-utils/format-date current-time) "; Offset hours=" offset-hours
         "; range-hours" range-hours]]))

(defn slider []
  [:input {:type "range" :min 1 :max 1200
           :class "slider"
           :style {:width (str (* 8 (:range-hours @(re-frame/subscribe [::subs/timer]))) "px")}
           :value (:offset-px @(re-frame/subscribe [::subs/timer]))
           :on-change #(re-frame/dispatch [:timer-change (-> % .-target .-value)])}])



(defn time-control []
    [:div
      [slider]
      [:div {:class "slider-scale"}
        [:div {:class "slider-tick tick-1"} "1st"]
        [:div {:class "slider-tick tick-2"} "2nd"]
        [:div {:class "slider-tick tick-3"} "3rd"]
        [:div {:class "slider-tick tick-4"} "4th"]
        [:div {:class "slider-tick tick-5"} "5th"]]])




(defn increment-time []
   (let [timer (re-frame/subscribe [::subs/timer])
         offset-px (:offset-px @timer)]
        (+ offset-px 5)))

(defn clockon []
      (js/setInterval #(if (and @(re-frame/subscribe [::subs/play-animation?])  (.hasFocus js/document))
                          (re-frame/dispatch [:timer-change (increment-time)])) 500))

(defn play-pause-button
  []
  (let [play-animation? @(re-frame/subscribe [::subs/play-animation?])]
   [:div {:class "play-button-group"}
     [:button {:on-click #(re-frame/dispatch [:handle-animation-play-change])
               :class (if play-animation?  "play-button paused"  "play-button")}]]))

(defn temperature-to-class
  [temp]
  (cond
     (< temp 0) "temp-very-cold"
     (< temp 5) "temp-cold"
     (< temp 10) "temp-chilly"
     (< temp 15) "temp-cool"
     (< temp 20) "temp-mild"
     (< temp 25) "temp-warm"
     (< temp 30) "temp-hot"
     :else "temp-very-hot"))

(defn precip-prob-to-class
       [pp]
       (cond
          (< pp 5) "pp-dry"
          (< pp 20) "pp-low"
          (< pp 50) "pp-moderate"
          (< pp 80) "pp-high"
          :else "pp-very-high"))



(defn temperature
  []
  (let [temperature (:T @(re-frame/subscribe [::subs/target-slice]))
        temp-feels-like (:F @(re-frame/subscribe [::subs/target-slice]))]
    [:div {:class (str "temperature "  (temperature-to-class temperature))}
     (str temperature \u00B0 "C " "(feels like " temp-feels-like  \u00B0 "C)")]))

(defn precipitation
  []
  (let [probability (:Pp @(re-frame/subscribe [::subs/target-slice]))]
    [:div {:class (precip-prob-to-class temperature)}
     "Rainfall probability=" probability]))


(defn main-panel []
    [:div
    ;;  [get-forecast-button]
    ;;  [sun-cmpt]
  ;;    [horizon]
    ;;  [forecast-raw]

      [time-status]
      [temperature]
      [precipitation]
      [time-control]
;;[time-labels]
      [play-pause-button]])

    ;;  [cloud-cartoon]])
    ;;  [get-location-button]
  ;;    [location-list]])
