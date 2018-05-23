(ns cast.customers
  (:require [cast.d3x :as d3x]
            ["d3" :as d3]
            [reagent.core :as r]))


(defn- customers-update [data x-scale y-scale]
  (let [svg     (.select d3 "#customers")
        line    (-> (.line d3)
                    (.curve (.-curveCatmullRom d3))
                    (.x #(-> % (aget "date") x-scale))
                    (.y #(-> % (aget "new") y-scale)))
        line2    (-> (.line d3)
                    (.curve (.-curveCatmullRom d3))
                    (.x #(-> % (aget "date") x-scale))
                    (.y #(-> % (aget "repeat") y-scale)))
        domain   (concat
                  (map #(aget % "repeat") data)
                  (map #(aget % "new") data))]
    (.domain x-scale (.extent d3 data #(aget % "date")))
    (.domain y-scale (d3x/padded domain 15))
    (d3x/x-axis svg x-scale "Date" (d3x/real-width 960) (d3x/real-height 560))
    (d3x/y-axis svg y-scale "Tickets")
    (-> (.append svg "path")
        (.datum data)
        (.attr "class" "line line-1")
        (.attr "d" line))
    (-> (.append svg "path")
        (.datum data)
        (.attr "class" "line line-2")
        (.attr "d" line2))))


(defn- customers-inner [data x-scale y-scale]
  (let [d3data (clj->js data)]
    (r/create-class
     {:reagent-render       (d3x/container "customers" 960 560)
      :component-did-mount #(customers-update d3data x-scale y-scale)
      :component-did-update #(customers-update d3data x-scale y-scale)})))


(defn customers [data]
  (let [x-scale (d3x/time-scale 0 (d3x/real-width 960))
        y-scale (d3x/linear-scale (d3x/real-height 560) 0)]
    (fn [] [customers-inner @data x-scale y-scale])))
