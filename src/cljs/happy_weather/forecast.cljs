(ns happy-weather.forecast
  (:require
   [happy-weather.date-utils :as date-utils]))

(defn create-3hr-forcast-data [day-report]
  (let [date (:value day-report)
        reports (:Rep day-report)]
       (map #(conj {:date (date-utils/parse-and-add-offset date (:$ %))} %) reports)))

(defn create-forecast-slices [raw-forecast]
   (apply concat (map create-3hr-forcast-data (:Period raw-forecast))))

(defn format-forecast [raw-forecast]
    (create-forecast-slices raw-forecast))

(defn get-target-slice [db timer]
  (let [forecast-slices (:forecast-slices db)]
     (first forecast-slices)))
