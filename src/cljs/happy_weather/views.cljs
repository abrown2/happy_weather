(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [happy-weather.subs :as subs]
   [happy-weather.orbit :as orbit]
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


(defn cloud-cartoon []
  (let [tilt (reagent/atom 0)
        rotation (anim/spring tilt)
        flip (reagent/atom 90)
        scale (anim/spring flip)
        size (reagent/atom 0)
        width (anim/spring size)]
    (fn a-logo-component []
      [:div
       {:style {:position "absolute" :top "200px" :right "500px"}}
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
  (let [sun-pos (reagent/atom {:sun-pos (orbit/orbit {:deg 130 :r 210})})
        x-pos (ratom/reaction (:x (:sun-pos @sun-pos)))
        x (anim/interpolate-to x-pos {:duration 100})
        y-pos (ratom/reaction (:y (:sun-pos @sun-pos)))
        y (anim/interpolate-to y-pos {:duration 100})]
   (fn create-sun []
      [:svg
       {:width 700
        :height 340}
       [anim/interval #(swap! sun-pos update :sun-pos orbit/orbit) 100]
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

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [wind-direction]
     [wind-direction-input]
     [sun-cmpt]
     [horizon]
     [cloud-cartoon]]))
    ;; [get-location-button]
    ;; [location-list]]))
