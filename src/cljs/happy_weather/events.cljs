(ns happy-weather.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx :as http]
   [happy-weather.db :as db]
   [ajax.core :as ajax]))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  :wind-direction-change
  (fn [db [_ new-wind-direction]]
    (assoc db :wind-direction new-wind-direction)))

(re-frame/reg-event-fx
  :location-retrieve
  (fn [{:keys [db]} _]
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist?"
                  :params          {:key "aeb6de8c-b9b7-4a94-a990-d0124810511a"}
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:handle-location-retrieve-success]
                  :on-failure      [:handle-location-retrieve-failure]}}))

(re-frame/reg-event-db
  :handle-location-retrieve-success
  (fn [db [_ locations]]
    (assoc db :locations locations)))
