(ns happy-weather.orbit
  [:require
   [re-frame.core :as re-frame]
   [happy-weather.subs :as subs]])


(defn add-offset
  [z]
  (+ z 320))

(defn calc-coord
  [r deg trig-fn]
  (*  r (trig-fn (* 0.01745 deg))))

(defn calc-x
  [r deg]
  (add-offset (calc-coord r deg Math/cos)))

(defn calc-y
  [r deg]
  (add-offset (calc-coord r deg Math/sin)))


(defn orbit
  [{:keys [x y r deg] :as sun-pos} offset-hours]
;;   {:keys [offset-hours] :as timer}]
  (let [new-deg (* offset-hours 15)]
     (do (.log js/console (str "new-deg=" new-deg))
         (into sun-pos {:x (calc-x r new-deg) :y (calc-y r new-deg) :r r :deg new-deg}))))


(defn update-orbit
  [{:keys [x y r deg] :as sun-pos} offset-hours]
  (let [timer (re-frame/subscribe [::subs/timer])
        offset-hours (:offset-hours @timer)
        new-deg (* offset-hours 15)]
     (do (.log js/console (str "new-deg=" new-deg))
         (into sun-pos {:x (calc-x r new-deg) :y (calc-y r new-deg) :r r :deg new-deg}))))
