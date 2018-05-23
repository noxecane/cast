(ns cast.drivers
  (:require [cast.data :as data]
            [cljs.spec.alpha :as s]
            [cljs.spec.gen.alpha :as gen]))


(defn drivers []
  (let [ds (gen/sample (s/gen ::data/driver) 4)]
    [:div.columns
     (for [{:keys [fullname phone license travelled]} ds]
       [:div.column {:key phone}
        [:div.card
         [:header.card-header>p.card-header-title fullname]
         [:div.card-content
          [:p [:span.bold "License: "] license]
          [:p [:span.bold "Phone Number:  "] phone]
          [:p [:span.bold "Distance Travelled: "] (float travelled)]]]])]))
