(ns happy-weather.forecast
  (:require
   [happy-weather.date-utils :as date-utils]
   [re-frame.core :as re-frame]
   [happy-weather.subs :as subs]
   [cljs-time.core :as time]))

(defn create-3hr-forcast-data [day-report]
  (let [date (:value day-report)
        reports (:Rep day-report)]
       (map #(conj {:date (date-utils/parse-and-add-offset date (:$ %))} %) reports)))

(defn create-forecast-slices [raw-forecast]
   (apply concat (map create-3hr-forcast-data (:Period raw-forecast))))

(defn format-forecast [raw-forecast]
    (create-forecast-slices raw-forecast))

(defn get-target-slice
  [{:keys [forecast-slices] } {:keys [current-time]}]
  (first (filter #(time/before? current-time (:date %)) forecast-slices)))
