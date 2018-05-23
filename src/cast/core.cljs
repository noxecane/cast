(ns cast.core
  (:require ["d3" :as d3]
            [cast.drivers :refer [drivers]]
            [cast.gen :as gen]
            [cast.setx :as csetx]
            [cast.time :refer [single multi]]
            [clojure.set :as cset]
            [clojure.string :refer [capitalize]]
            [reagent.core :as r]
            [reagent.ratom :refer [reaction]]))

(enable-console-print!)

(defonce svg-width 960)
(defonce svg-height 560)
(defonce schedule [6 12 18])
(defonce terminals [:alaba :jibuwo :udoka :portharcourt :ikotun :aba])
(defonce generated (gen/gen-trips schedule terminals 7 10 20))

(defn header []
  [:header.navbar
   [:div.container
    [:div.navbar-brand
     [:a.navbar-item {:href "/"} "Cast"]]]])


(defn total [data]
  (for [[date entries] (cset/index data [:date])]
    (reduce (fn [sum-entry {:keys [total new repeat]}]
              (-> sum-entry
                  (update :total + total)
                  (update :new + new)
                  (update :repeat + repeat)))
            (-> entries
                (cset/project [:date :new :repeat])
                (csetx/extend (fn [{:keys [new repeat]}] {:total (+ new repeat)}))))))



(defonce app-state (r/atom {:totals (total generated)}))

(defn filtered [key]
  (println key)
  (swap! app-state assoc :totals (total (cset/select #(-> % :terminal (= (keyword key))) generated))))


;; [:div.field
;;  [:label.label.has-text-left "Terminal"]
;;  [:div.control
;;   [:div.select
;;    [:select {:on-change #(-> % .-target .-value filtered)}
;;     (for [t terminals]
;;       [:option {:value t :key t} (str (capitalize (name t)) " Terminal")])]]]]

(defn traffic [data]
  [single data {:id         "traffic"
                :width      svg-width
                :height     svg-height
                :x-label    "Trip Date"
                :y-label    "Total Trips"
                :line-class "date-total"
                :x          #(aget % "date")
                :y          #(aget % "total")}
   [:dot-line "dot" 5]])


(defn customers [data]
  [multi data {:id           "customers"
               :width        svg-width
               :height       svg-height
               :x-label      "Trip Date"
               :y-label      "Passengers"
               :line-class   "date-trips"
               :legend-class "trip-types"
               :x            #(aget % "date")
               :ys           [{:key   #(aget % "new")
                               :label "New"
                               :class "new"}
                              {:key   #(aget % "repeat")
                               :label "Repeat"
                               :class "repeat"}]}])


(defn page []
  (let [totals (reaction (->> @app-state :totals (sort-by :date)))]
    (fn []
      [:div.hero.is-light.is-fullheight
       [:div.hero-head (header)]
       [:div.hero-body
        [:div.container.has-text-centered
         [:h1.title "Ticket Rate"]
         [:div.chart [traffic totals]]
         [:h1.title "Passenger Growth"]
         [:div.chart [customers totals]]]]])))


(defn ^:dev/after-load render-reagent []
  (r/render [page] (.getElementById js/document "app")))

(render-reagent)
