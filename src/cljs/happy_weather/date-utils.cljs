(ns happy-weather.date-utils
  (:require
   [cljs-time.core :as time]
   [cljs-time.format :as time-format]
   [cljs-time.local :as time-local]))


(def default-time-format (time-format/formatter "E ha, do MMM yy"))

(defn format-date
  [date]
  (time-format/unparse-local default-time-format date))

(defn current-time []
  (format-date (time-local/local-now)))


(def default-start-time (time/date-time 2018 11 01 12 00 00))
(def default-end-time (time/date-time 2018 11 05 9 00 00))

(defn add-hours
  [time hours]
  (time/plus time (time/hours hours)))
