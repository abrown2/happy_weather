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
        offset-hours (:offset-hours @timer)]
    [:div
      [:p "current-time=" (date-utils/format-date current-time) "; Offset hours=" offset-hours]]))

(defn slider [min max]
  [:input {:type "range" :min min :max max
           :style {:width "100%"}
           :value (:offset-px @(re-frame/subscribe [::subs/timer]))
           :on-change #(re-frame/dispatch [:timer-change (-> % .-target .-value)])}])


(defn time-control []
    [:div
      [slider 1 600]])

(def isPlay (reagent/atom false))

(def playLocked? (reagent/atom false))
(defn setTimeoutLock
  [lock]
  (do (reset! lock true)
      (js/setTimeout #(reset! lock false) 20)))


(defn increment-time []
   (let [timer (re-frame/subscribe [::subs/timer])
         offset-px (:offset-px @timer)]
      (do ;;(.log js/console (str "old-offset=" offset-px))
          (+ offset-px 5))))

(defn clockon []
      (js/setInterval #(if @isPlay (re-frame/dispatch [:timer-change (increment-time)])) 500))

(defn play-pause-button
  []
  [:div
   [:p
     [:button {:on-click  (if (true? @playLocked?)
                            (do (setTimeoutLock playLocked?)
                                (re-frame/dispatch [:handle-animation-play-change])))}  ;; toggle play animation
        (if @(re-frame/subscribe [::subs/play-animation?]) "||" ">")]]])



(defn temperature
  []
  [:div
     [:p "temp="  (:T @(re-frame/subscribe [::subs/target-slice]))]])


(defn main-panel []
    [:div
    ;;  [get-forecast-button]
    ;;  [sun-cmpt]
    ;;  [horizon]
    ;;  [forecast-raw]

    ;;  [time-status]
      [temperature]
      [time-control]
      [play-pause-button]])

    ;;  [cloud-cartoon]])
    ;;  [get-location-button]
  ;;    [location-list]])
