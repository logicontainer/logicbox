"use client"

// app/gallery/page.tsx
import dynamic from 'next/dynamic';

const GalleryPage = dynamic(() => import('@/components/GalleryPage'), {
  ssr: false,
});

export default function Page() {
  return <GalleryPage />;
}
