(ns vis.cljs)

;; (def data
;;   (map #(-> [(str %1) %2])
;;    (seq "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
;;    (gen/sample (s/gen (s/and int? pos? #(< % 50))) 26)))

;; (def width 960)
;; (def height 500)
;; (def margin {:top 20 :right 30 :bottom 30 :left 40})
;; (def inner-width (- width (:left margin) (:right margin)))
;; (def inner-height (- height (:top margin) (:bottom margin)))

;; (defn translate [x y] (str "translate(" x "," y ")"))

;; (def x (-> (.scaleBand d3)
;;            (.domain (->> data (map first) sort clj->js))
;;            (.rangeRound #js [0 inner-width])
;;            (.padding 0.1)))

;; (def y (-> (.scaleLinear d3)
;;            (.domain #js [0, (.max d3 (->> data (map second) clj->js))])
;;            (.range #js [inner-height 0])))

;; (def x-axis (-> (.axisBottom d3) (.scale x)))
;; (def y-axis (-> (.axisLeft d3) (.scale y) (.ticks 10 "%")))

;; (def chart (-> (.select d3 ".chart")
;;                (.attr "width" width)
;;                (.attr "height" height)
;;                (.append "g")
;;                (.attr "transform" (translate (:left margin) (:top margin)))))

;; (-> (.append chart "g")
;;     (.attr "class" "x axis")
;;     (.attr "transform" (translate 0 inner-height))
;;     (.call x-axis))

;; (-> (.append chart "g")
;;     (.attr "class" "y axis")
;;     (.call y-axis)
;;     (.append "text")
;;     (.attr "transform" "rotate(-90)")
;;     (.attr "y" 9)
;;     (.attr "dy" "0.71em")
;;     (.style "text-anchor" "end")
;;     (.text "Frequency"))

;; (-> (.selectAll chart ".bar")
;;     (.data (clj->js data))
;;     (.enter)
;;     (.append "rect")
;;     (.attr "class" "bar")
;;     (.attr "x" #(-> % first x))
;;     (.attr "y" #(-> % second y))
;;     (.attr "height" #(->> % second y (- inner-height)))
;;     (.attr "width" (.bandwidth x)))
