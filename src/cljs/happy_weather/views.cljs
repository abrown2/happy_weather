(ns happy-weather.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [happy-weather.subs :as subs]
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

;;(defn spinner []
;;  (let [spinner (aget js/window "Spinner")]
;;   [(reagent/adapt-react-class spinner)
;;    (js-obj lines: 99)))    ;; need a js object as spinner mutates it


(defn cloud-cartoon []
  (let [tilt (reagent/atom 0)
        rotation (anim/spring tilt)
        flip (reagent/atom 90)
        scale (anim/spring flip)
        size (reagent/atom 0)
        width (anim/spring size)]
    (fn a-logo-component []
      [:div
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

(defn bike-component [bike]
  (let [ba (reagent/atom bike)
        xa (ratom/reaction (:x @ba))
        x (anim/interpolate-to xa {:duration 1000})]
    (fn [{:keys [color y size] :as bike}]
      (reset! ba bike)
      [:g
       {:transform (str "translate(" @x " " (str (+ y (rand-int 23))) ")")}
       [:circle
        {:r size
         :cx 20 :cy 20
         :fill color}]
       [:circle
        {:r size
         :cx 40 :cy 20
         :fill color}]
       [:path
        {:stroke color
         :fill "none"
         :d "M25 10 L35 10 L40 20 L30 20 L25 10
              M20 20 L30 0
              M30 20 L40 5"}]])))

(defn one-bike [{:keys [dx] :as bike}]
  (update bike :x (fn [x]
                    (-> (+ x dx)
                        (mod 500)))))

(defn bike-step [bikes]
  (into bikes
        (for [[k v] bikes]
          [k (one-bike v)])))

(defn new-bike []
  {:size (+ 5 (rand-int 23))
   :dx (* (rand-nth [1 -1])
          (+ 5 (rand-int 15)))
   :color (rand-nth ["red" "green" "blue" "gold"])
   :x (rand-int 500)
   :y (rand-int 100)})

(defn bikes[]
  (let [app-state (reagent/atom
                    {:bikes (zipmap (repeatedly gensym)
                                    (repeatedly 10 new-bike))})]
    (fn a-react-to-value-example-component []
      [:svg
       {:width 560
        :height 120}
       [anim/interval #(swap! app-state update :bikes bike-step) 5000]
       (for [[k v] (:bikes @app-state)]
         ^{:key k}
         [bike-component v])])))
(defn add-offset
  [z]
  (+ z 140))

(defn calc-x
  [r deg]
  (add-offset (*  r (Math/cos (* 0.01745 deg)))))

(defn calc-y
  [r deg]
  (add-offset (*  r (Math/sin (* 0.01745 deg)))))


(defn orbit
  [{:keys [x y r deg] :as sun-pos}]
  (do (.log js/console (str "x=" x " y=" y " deg=" deg " r=" r))
      (let [new-deg (+ deg 10)]
        (into sun-pos {:x (calc-x r new-deg) :y (calc-y r new-deg) :r r :deg new-deg}))))

(defn sun-cmpt[]
  (let [sun-pos (reagent/atom {:sun-pos {:x 20 :y 0 :deg 0 :r 140}})
        x-pos (ratom/reaction (:x (:sun-pos @sun-pos)))
        x (anim/interpolate-to x-pos {:duration 1000})
        y-pos (ratom/reaction (:y (:sun-pos @sun-pos)))
        y (anim/interpolate-to y-pos {:duration 1000})]
   (fn create-sun []
      ;;(reset! sun-pos {:x 20 :y 0})
      [:svg
       {:width 560
        :height 320}
       [anim/interval #(swap! sun-pos update :sun-pos orbit) 1000]
       [:g
        {:transform (str "translate(" @x " " @y ")")}
        [:circle
         {:r 30
          :cx 120 :cy 120
          :fill "gold"}]]])))
    ;;   [sun-svg]])




(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [wind-direction]
     [wind-direction-input]
     [:p]
     [sun-cmpt]
     ;;[bikes]
     [:p]
     [cloud-cartoon]
     [:p]]))
    ;; [get-location-button]
    ;; [location-list]]))
