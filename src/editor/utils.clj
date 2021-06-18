(ns editor.utils
  )


(defmacro case-enum
  "Like `case`, but explicitly dispatch on Java enum ordinals."
  [e & clauses]
  (letfn [(enum-ordinal [e] `(let [^Enum e# ~e] (.ordinal e#)))]
    `(case ~(enum-ordinal e)
       ~@(concat
          (mapcat (fn [[test result]]
                    [(eval (enum-ordinal test)) result])
                  (partition 2 clauses))
          (when (odd? (count clauses))
            (list (last clauses)))))))

(defmacro case-with-eval
  "Like `case`, but test-constants *are* evaluated.
  Now we can do
  (case-with-eval 2
    (+ 1 1) \"yay\"
    \"nay\")
  "
  [e & clauses]

  `(case ~e
       ~@(concat
          (mapcat (fn [[test result]]
                    [(eval test) result])
                  (partition 2 clauses))
          (when (odd? (count clauses))
            (list (last clauses))))))

