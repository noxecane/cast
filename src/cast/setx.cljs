(ns cast.setx)

(defn extend [xrel f]
  (with-meta (set (map #(merge % (f %)) xrel)) (meta xrel)))
