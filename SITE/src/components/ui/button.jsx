import * as React from "react"
import { cva } from "class-variance-authority"
import { motion } from "framer-motion"
import { cn } from "../../lib/utils"

const buttonVariants = cva(
  "inline-flex items-center justify-center rounded-lg text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-background disabled:pointer-events-none disabled:opacity-50 touch-manipulation min-h-[44px] sm:min-h-0",
  {
    variants: {
      variant: {
        default:
          "bg-white text-black hover:bg-zinc-100 hover:shadow-lg hover:shadow-white/10 focus-visible:ring-white transition-all duration-200",
        primary:
          "btn-primary border-0 focus-visible:ring-purple-500 focus-visible:ring-offset-purple-900/50",
        outline:
          "border border-purple-500/30 bg-transparent text-[#c4c1d6] hover:bg-purple-500/10 hover:border-purple-500/50 focus-visible:ring-purple-500",
        ghost: "hover:bg-purple-500/10 focus-visible:ring-purple-500",
      },
      size: {
        default: "h-10 px-4 py-2",
        sm: "h-9 px-3",
        lg: "h-12 px-8 text-base",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

const Button = React.forwardRef(
  ({ className, variant, size, asChild = false, children, ...props }, ref) => {
    const classes = cn(buttonVariants({ variant, size, className }))
    if (asChild && React.Children.only(children)) {
      return React.cloneElement(children, {
        ...props,
        className: cn(classes, children.props?.className),
        ref: ref || children.ref,
      })
    }
    return (
      <motion.button
        className={classes}
        ref={ref}
        whileHover={{ y: -2, scale: 1.03 }}
        whileTap={{ scale: 0.97, y: 0 }}
        transition={{ type: "spring", stiffness: 380, damping: 24 }}
        {...props}
      >
        {children}
      </motion.button>
    )
  }
)
Button.displayName = "Button"

export { Button, buttonVariants }
