import { Children, cloneElement, isValidElement } from 'react';
import type { ReactElement, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { ButtonProps } from './button';

interface ButtonGroupProps {
  className?: string;
  orientation?: 'horizontal' | 'vertical';
  children: ReactNode;
}

export const ButtonGroup = ({
  className,
  orientation = 'horizontal',
  children,
}: ButtonGroupProps) => {
  const isHorizontal = orientation === 'horizontal';

  return (
    <div
      className={cn(
        'inline-flex',
        {
          'flex-col': !isHorizontal,
        },
        className
      )}
    >
      {Children.map(children, (child) => {
        if (!isValidElement(child)) {
          return child;
        }

        const childElement = child as ReactElement<ButtonProps>;

        return cloneElement(childElement, {
          className: cn(
            // Apply the button's original classes FIRST
            childElement.props.className,

            // Apply all of our group-specific overrides LAST
            'rounded-none focus:z-10',
            {
              '-ml-px': isHorizontal,
              '-mt-px': !isHorizontal,
            },
            'first:ml-0 first:mt-0',
            
            // --- THIS IS THE FIX ---
            // Be explicit about first/last element rounding instead of combining them
            {
              'first:rounded-l-md': isHorizontal,
              'last:rounded-r-md': isHorizontal,
              'first:rounded-t-md': !isHorizontal,
              'last:rounded-b-md': !isHorizontal,
            }
          ),
        });
      })}
    </div>
  );
};
