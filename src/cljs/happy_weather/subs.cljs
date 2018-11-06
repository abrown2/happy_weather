(ns happy-weather.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::wind-direction
  (fn [db]
    (:wind-direction db)))

(re-frame/reg-sub
  ::locations
  (fn [db]
    (:locations db)))

(re-frame/reg-sub
  ::forecast-slices
  (fn [db]
    (:forecast-slices db)))


(re-frame/reg-sub
  ::target-slice
  (fn [db]
    (:target-slice db)))

(re-frame/reg-sub
  ::timer
  (fn [db]
    (:timer db)))
