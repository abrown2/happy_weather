(ns happy-weather.db
  (:require
   [happy-weather.date-utils :as date-utils]
   [reagent.core :as reagent]))


(def default-db
  {:name "re-frame"
   :wind-direction 110
   :locations {:Location [{:elevation "50.0" :id "14" :latitude "54.9375" :longitude "-2.8092" :name "Carlisle Airport" :region "nw" :unitaryAuthArea "Cumbria"}]}
   :play-animation? (reagent/atom false)
   :timer {:start-time date-utils/default-start-time
           :end-time date-utils/default-end-time
           :offset-px 0
           :offset-hours 0
           :current-time (date-utils/add-hours date-utils/default-start-time 5)}})
