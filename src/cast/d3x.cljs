(ns cast.d3x
  (:require ["d3" :as d3]))


(def label-pad 9)
(def margin-left 40)
(def margin-right 30)
(def margin-top 20)
(def margin-bottom 30)


(defn real-width [w]
  (- w margin-left margin-right))


(defn real-height [h]
  (- h margin-top margin-bottom))


(defn padded
  ([data pad]
   (padded data pad pad))
  ([data top-pad bottom-pad]
   (let [min-value (apply min data)
         max-value (apply max data)]
     (clj->js [(max 0 (- min-value bottom-pad)) (+ max-value top-pad)]))))


(defn container [id width height]
  (fn []
    [:svg.d3 {:width width :height height}
     [:g {:id id :transform (str "translate(" margin-left "," margin-top ")")}]]))


(defn time-scale [start end]
  (-> (.scaleTime d3)
      (.range #js [start end])))


(defn linear-scale [start end]
  (-> (.scaleLinear d3)
      (.range #js [start end])))


(defn x-axis
  [parent scale label w h]
  (-> (.append parent "g")
      (.attr "transform" (str "translate(" 0 "," h ")"))
      (.call (.axisBottom d3 scale)))
  (-> (.append parent "text")
      (.attr "x" w)
      (.attr "y" (- h label-pad))
      (.attr "class" "axis x")
      (.text label)))


(defn y-axis [parent scale label]
  (-> (.append parent "g")
      (.call (.axisLeft d3 scale)))
  (-> (.append parent "text")
      (.attr "transform" "rotate(-90)")
      (.attr "y" label-pad)
      (.attr "dy" "0.71em")
      (.attr "class" "axis y")
      (.text label)))


(defn line [parent data line-class fx fy]
  (-> (.append parent "path")
      (.datum data)
      (.attr "class" line-class)
      (.attr "d" (-> (.line d3)
                     (.curve (.-curveCatmullRom d3))
                     (.x fx)
                     (.y fy)))))


(defn dot-line [parent data dot-class r fx fy]
  (-> (.selectAll parent dot-class)
      (.data data)
      (.enter)
      (.append "circle")
      (.attr "class" dot-class)
      (.attr "r" r)
      (.attr "cx" fx)
      (.attr "cy" fy)))


(defn dot-label [parent dot-class r x y label]
  (-> (.append parent "circle")
      (.attr "class" dot-class)
      (.attr "r" r)
      (.attr "cx" x)
      (.attr "cy" y))
  (-> (.append parent "text")
      (.attr "x" (+ x r 2))
      (.attr "y" y)
      (.attr "dy" "0.35em")
      (.attr "class" "dot-label")
      (.text label)))

(defn horizontal-line [parent line-class x y width]
  (-> (.append parent "line")
      (.attr "class" line-class)
      (.attr "x1" x)
      (.attr "x2" (+ x width))
      (.attr "y1" y)
      (.attr "y2" y)))


(defmulti extension #(first %1))

(defmethod extension :dot-line
  [[_ dot-class r] data {x :x y :y} parent x-scale y-scale]
  (dot-line parent data dot-class r (comp x-scale x) (comp y-scale y)))


(defn extensions [extensions data opts parent x-scale y-scale]
  (doseq [ext extensions]
    (extension ext data opts parent x-scale y-scale)))
