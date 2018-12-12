(ns happy-weather.date-utils
  (:require
   [cljs-time.core :as time]
   [cljs-time.format :as time-format]
   [cljs-time.local :as time-local]))


(def default-time-format (time-format/formatter "E ha, do MMM yy"))

(def default-date-format (time-format/formatter "YYYY-MM-DDZ"))

(defn format-date
  [date]
  (time-format/unparse-local default-time-format date))

(defn current-time []
  (format-date (time-local/local-now)))

(defn parse-date [date]
  (time-format/parse-local default-date-format date))

(defn parse-and-add-offset
  [date offset-mins]
  (time/plus (parse-date date) (time/minutes offset-mins)))

(def default-start-time (time/date-time 2018 11 07 12 00 00))
(def default-end-time (time/date-time 2018 11 11 9 00 00))

(defn interval-hours
  [start end]
  (time/in-hours (time/interval start end)))

(defn add-hours
  [time hours]
  (time/plus time (time/hours hours)))
