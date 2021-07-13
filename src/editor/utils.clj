(ns editor.utils
  "Utilities that have nothing to do with java awt"
  (:require [clojure.test :refer :all]))


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

(defn number-of-digits
  "Returns the number of digits in `n`.
  Only really works for positive `n`."
  [n]
  (count (str n)))

(defmacro is= [a b]
  `(is (= ~a ~b)))

(with-test #'number-of-digits
  (is= (number-of-digits 42) 2)
  (is= (number-of-digits 0) 1)
  (is= (number-of-digits 1) 1)
  ;; (is= (number-of-digits 1) 2)
  (is= (number-of-digits 10) 2)
  (is= (number-of-digits 123) 3)
  )

(defn clamp
  "Clamps the value of `x` between `low` and `high`.

  The returned value is between `low` and `high`.
  
  Returns `low` if `x` is less than `low`.
  Returns `high` if `x` is greater than `high`.
  Returns `x` unmodified otherwise."
  [x low high]
  (max (min high x) low))


(with-test #'clamp
  (is= (clamp 1 2 3) 2)
  (is= (clamp 2 2 3) 2)
  (is= (clamp 3 2 4) 3)
  (is= (clamp 4 2 4) 4)
  (is= (clamp 6 2 4) 4)

  ;; (is= (clamp 6 2 4) 8)
  )
