(ns happy-weather-temp.events
  (:require
   [re-frame.core :as re-frame]
   [happy-weather-temp.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
