(ns happy-weather.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx :as http]
   [happy-weather.db :as db]
   [ajax.core :as ajax]
   [happy-weather.date-utils :as date-utils]
   [happy-weather.config :as config]
   [happy-weather.forecast :as forecast]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  :wind-direction-change
  (fn [db [_ new-wind-direction]]
    (assoc db :wind-direction new-wind-direction)))

(defn hours-from-px
  [px]
  (/ px (:px-per-hour config/view-config)))

(defn update-timer
  [{:keys [start-time] :as timer} new-offset]
  (let [ new-offset (mod new-offset 560)
        updated-values {:current-time (date-utils/add-hours start-time (hours-from-px new-offset))
                        :offset-px new-offset
                        :offset-hours (hours-from-px new-offset)}]
    (merge timer updated-values)))

(defn reset-timer
  [slices]
  {:offset-px 0
               :offset-hours 0
               :start-time (:date (first slices))
               :current-time (:date (first slices))
               :end-time (:date (last slices))})


(re-frame/reg-event-db
  :timer-change
  (fn [db [_ new-val]]
     (let [timer (:timer db)
           new-timer (update-timer timer new-val)]
        (assoc db :timer new-timer :target-slice (forecast/get-target-slice db new-timer)))))


(re-frame/reg-event-fx
  :location-retrieve
  (fn [{:keys [db]} _]
    {:http-xhrio {:method          :get
                  :uri             "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist?"
                  :params          {:key "aeb6de8c-b9b7-4a94-a990-d0124810511a"}
                  :timeout         8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:handle-location-retrieve-success]
                  :on-failure      [:handle-location-retrieve-failure]}}))

(re-frame/reg-event-fx
  :forecast-retrieve
  (fn [{:keys [db]} _]
    {:http-xhrio {:method          :get
                  :uri             "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/353207?"
                  :params          {:key "aeb6de8c-b9b7-4a94-a990-d0124810511a" :res "3hourly"}
                  :timeout         8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:handle-forecast-retrieve-success]
                  :on-failure      [:handle-forecast-retrieve-failure]}}))


(re-frame/reg-event-db
  :handle-location-retrieve-success
  (fn [db [_ response]]
    (assoc db :locations (:Locations response))))

(re-frame/reg-event-db
  :handle-forecast-retrieve-success
  (fn [db [_ response]]
    (let [slices (forecast/create-forecast-slices (:Location (:DV (:SiteRep response))))]
      ( -> db
        (assoc :forecast-slices slices)
        (assoc :timer (reset-timer slices))))))
