(ns happy-weather.events
  (:require
   [re-frame.core :as re-frame]
   [happy-weather.db :as db]))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  :wind-direction-change
  (fn [db [_ new-wind-direction]]
    (assoc db :wind-direction new-wind-direction)))
