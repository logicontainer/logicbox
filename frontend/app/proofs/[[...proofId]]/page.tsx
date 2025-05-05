import Client from "./client";

type PageParams = {
  params: Promise<{
    proofId?: string[]; // Because it's catch-all (array or undefined)
  }>;
};

export default async function Page({ params }: PageParams) {
  const { proofId: proofIdArray } = await params;
  const proofId = proofIdArray?.[0] || null;

  return <Client proofId={proofId} />;
}
