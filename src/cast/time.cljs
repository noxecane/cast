(ns cast.time
  (:require [cast.d3x :as d3x]
            ["d3" :as d3]
            [reagent.core :as r]))


(defn single [datom {:keys [id width height x-label y-label line-class x y] :as opts} & extensions]
  (let [real-width  (d3x/real-width width)
        real-height (d3x/real-height height)
        x-scale     (d3x/time-scale 0 real-width)
        y-scale     (d3x/linear-scale real-height 0)
        fx          (comp x-scale x)
        fy          (comp y-scale y)
        draw        (fn [data]
                      (let [parent (.select d3 (str "#" id))]
                        (.domain x-scale (.extent d3 data x))
                        (.domain y-scale (d3x/padded (map y data) 15))
                        (d3x/x-axis parent x-scale x-label real-width real-height)
                        (d3x/y-axis parent y-scale y-label)
                        (d3x/line parent data line-class fx fy)
                        (d3x/extensions extensions data opts parent x-scale y-scale)))
        react       (fn [data]
                      (let [js-data (clj->js data)]
                        (r/create-class
                         {:reagent-render       (d3x/container id width height)
                          :component-did-mount  #(draw js-data)
                          :component-did-update #(draw js-data)})))]
    #(-> [react @datom])))


(defn multi [datom {:keys [id width height x-label y-label line-class legend-class x ys] :as opts} & extensions]
  (let [real-width   (d3x/real-width width)
        real-height  (d3x/real-height height)
        x-scale      (d3x/time-scale 0 real-width)
        y-scale      (d3x/linear-scale real-height 0)
        label-height 25
        fx           (comp x-scale x)
        draw         (fn [data]
                       (let [parent   (.select d3 (str "#" id))
                             legend   (-> (.append parent "g")
                                          (.attr "class" legend-class))
                             y-domain (flatten (map (comp #(map % data) :key) ys))
                             max-y    (apply max y-domain)]
                         (.domain x-scale (.extent d3 data x))
                         (.domain y-scale (d3x/padded y-domain (* (count ys) label-height) 15))
                         (d3x/x-axis parent x-scale x-label real-width real-height)
                         (d3x/y-axis parent y-scale y-label)
                         (loop [ys ys, n (count ys)]
                           (if-not (zero? n)
                             (let [{:keys [key label class]} (first ys)
                                   l-class                   (str line-class " " class)
                                   fy                        (comp y-scale key)
                                   x                         (- real-width 100)
                                   y                         (+ 15 (* label-height n))]
                               (d3x/line parent data l-class fx fy)
                               (d3x/dot-label legend class 5 x y label)
                               (recur (next ys) (dec n)))))
                         (d3x/extensions extensions data opts parent x-scale y-scale)))
        react        (fn [data]
                       (let [js-data (clj->js data)]
                         (r/create-class
                          {:reagent-render       (d3x/container id width height)
                           :component-did-mount  #(draw js-data)
                           :component-did-update #(draw js-data)})))]
    #(-> [react @datom])))
