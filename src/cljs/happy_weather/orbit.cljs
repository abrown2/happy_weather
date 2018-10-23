(ns happy-weather.orbit)

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
  [{:keys [x y r deg] :as sun-pos}]
  (let [new-deg (+ deg 10)]
    (into sun-pos {:x (calc-x r new-deg) :y (calc-y r new-deg) :r r :deg new-deg})))
